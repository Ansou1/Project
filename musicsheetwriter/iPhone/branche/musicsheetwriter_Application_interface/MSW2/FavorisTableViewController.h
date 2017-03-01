//
//  FavorisTableViewController.h
//  MSW
//
//  Created by simon on 15/12/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FavorisTableViewController : UITableViewController

@property (strong, nonatomic) NSMutableArray *pseudo;
@property (strong, nonatomic) NSMutableArray *idRow;
@property (strong, nonatomic) NSMutableArray *idRow2;
@property (strong, nonatomic) NSMutableArray *suscribeRow;

@property (strong, nonatomic) IBOutlet UITableView *table1;
@end
