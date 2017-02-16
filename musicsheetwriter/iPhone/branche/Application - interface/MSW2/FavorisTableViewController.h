//
//  FavorisTableViewController.h
//  MSW
//
//  Created by simon on 15/12/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FavorisTableViewController : UITableViewController

@property (strong, nonatomic) NSMutableArray *idRow;
@property (strong, nonatomic) NSMutableArray *urlMusicSheet;
@property (strong, nonatomic) NSMutableArray *result;
@property (strong, nonatomic) NSMutableArray *titleMusicSheet;
@property (strong, nonatomic) NSMutableArray *userMusicSheet;

@property (strong, nonatomic) IBOutlet UITableView *table1;

@end
