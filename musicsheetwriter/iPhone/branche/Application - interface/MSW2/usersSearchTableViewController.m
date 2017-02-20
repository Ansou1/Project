//
//  usersSearchTableViewController.m
//  MSW2
//
//  Created by simon on 22/10/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import "usersSearchTableViewController.h"
#import "ProfilViewController.h"

@interface usersSearchTableViewController ()

@end

@implementation usersSearchTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.searchBar.delegate = self;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

-(void) searchBarSearchButtonClicked:(UISearchBar *)searchBar{
    [self refreshView];
}

-(void) refreshView{
    ApiMethod *api = [[ApiMethod alloc]init];
    NSString *post1 = [NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users?uname=%@", self.searchBar.text];
    NSString *post = [post1 stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
    NSDictionary *dict1 = [api ApiMethodGetAfnetworkingAt:post];

    if (code_global != 200){
        [api popup:dict1 id:self];
        return;
    }
    _pseudo = [[NSMutableArray alloc] initWithCapacity:0];
    _result = [[NSMutableArray alloc] initWithCapacity:0];
    _idRow = [[NSMutableArray alloc] initWithCapacity:0];
    
    for(NSDictionary *item in dict1) {
        [_pseudo addObject:[item valueForKey:@"username"]];
        [_idRow addObject:[item valueForKey:@"id"]];
    }
    [self.tableView reloadData];
    
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return _pseudo.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *cellId = @"SubscriptionCell";
    UITableViewCell *cell2 = [tableView dequeueReusableCellWithIdentifier:cellId forIndexPath:indexPath];
    cell2.textLabel.text = self.pseudo[indexPath.row];
    return cell2;
}

-(void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    if ([[segue identifier] isEqualToString:@"ShowProfil"]){
        ProfilViewController *profilController = [segue destinationViewController];
        NSIndexPath *myIndexPath = nil;
        myIndexPath = [self.tableView indexPathForSelectedRow];
        profilController.ProfilModal = @[_pseudo[myIndexPath.row], _idRow[myIndexPath.row], _idRow[myIndexPath.row]];
    }
}

@end
