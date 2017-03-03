//
//  SuscriberTableViewController.h
//  MSW
//
//  Created by simon on 15/12/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SubscriptionCell.h"

@interface SuscriberTableViewController : UITableViewController

@property (strong, nonatomic) NSMutableArray *pseudo;
@property (strong, nonatomic) NSMutableArray *idRow;
@property (strong, nonatomic) IBOutlet UIScrollView *scrollView;

@end
